package com.example.ptweb.controller.torrent;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ptweb.config.SiteBasicConfig;
import com.example.ptweb.config.TrackerConfig;
import com.example.ptweb.controller.dto.response.*;
import com.example.ptweb.controller.torrent.dto.request.SearchTorrentRequestDTO;
import com.example.ptweb.controller.torrent.dto.request.TorrentScrapeRequestDTO;
import com.example.ptweb.controller.torrent.dto.response.TorrentScrapeResponseDTO;
import com.example.ptweb.controller.torrent.dto.response.TorrentSearchResultResponseDTO;
import com.example.ptweb.controller.torrent.dto.response.TorrentUploadSuccessResponseDTO;
import com.example.ptweb.controller.torrent.form.TorrentUploadForm;
import com.example.ptweb.entity.*;
import com.example.ptweb.exception.APIGenericException;
import com.example.ptweb.exception.EmptyTorrentFileException;
import com.example.ptweb.exception.InvalidTorrentVersionException;
import com.example.ptweb.exception.TorrentException;
import com.example.ptweb.other.ResponsePojo;
import com.example.ptweb.service.*;
import com.example.ptweb.util.IPUtil;
import com.example.ptweb.util.TorrentParser;
import com.example.ptweb.util.URLEncodeUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.ptweb.exception.APIErrorCode.*;

@RestController
@RequestMapping("/torrent")
@Slf4j
public class TorrentController {
    @Autowired
    private TorrentService torrentService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private UserService userService;
    @Autowired
    @Qualifier("torrentsDirectory")
    private File torrentsDirectory;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private TransferHistoryService transferHistoryService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private PolicyFactory sanitizeFactory;
    @Autowired
    private TagService tagService;
    @Autowired
    private AuthenticationService authenticationService;
    //@Autowired
    //private ThanksService thanksService;

    @PostMapping("/upload")
    @SaCheckLogin
    @Transactional
    public ResponseEntity<ResponsePojo> upload(@Valid @ModelAttribute TorrentUploadForm form) throws IOException {
        log.info("Upload torrent {}", form);
        if (StringUtils.isEmpty(form.getTitle())) {
            throw new APIGenericException(MISSING_PARAMETERS, "You must provide a title.");
        }
        if (StringUtils.isEmpty(form.getDescription())) {
            throw new APIGenericException(MISSING_PARAMETERS, "You must provide a description.");
        }
        if (form.getFile() == null || form.getFile().isEmpty()) {
            throw new APIGenericException(INVALID_TORRENT_FILE, "You must provide a valid torrent file.");
        }
        form.setDescription(sanitizeFactory.sanitize(form.getDescription()));
        User user = userService.getUser(StpUtil.getLoginIdAsLong());
        Category category = categoryService.getCategory(form.getCategory());
        PromotionPolicy promotionPolicy = promotionService.getDefaultPromotionPolicy();
        SiteBasicConfig siteBasicConfig = settingService.get(SiteBasicConfig.getConfigKey(), SiteBasicConfig.class);
        if (category == null) {
            throw new APIGenericException(INVALID_CATEGORY, "Specified category not exists.");
        }
        if (user == null) {
            throw new IllegalStateException("User cannot be null at this time");
        }
        String publisher = user.getUsername();
        String publisherUrl = siteBasicConfig.getSiteBaseURL() + "/user/" + user.getId();
        List<Tag> tags = new ArrayList<>();
        for (String tag : form.getTag()) {
            Tag t = tagService.getTag(tag);
            tags.add(Objects.requireNonNullElseGet(t, () -> tagService.save(new Tag(0, tag))));
        }
        try {
            TorrentParser parser = new TorrentParser(form.getFile().getBytes(), true);
            parser.rewriteForTracker(siteBasicConfig.getSiteName(), publisher, publisherUrl);
            String infoHash = parser.getInfoHash();
            if (torrentService.getTorrentByInfoHash(infoHash) != null) {
                throw new APIGenericException(TORRENT_ALREADY_EXISTS, "The torrent's info_hash has been exists on this tracker.");
            }
            Files.write(new File(torrentsDirectory, infoHash + ".torrent").toPath(), parser.save());
            Torrent torrent = null;
            if (promotionPolicy != null) {
                torrent = new Torrent(null, infoHash, user.getId(), form.getTitle(),
                        form.getSubtitle(), parser.getTorrentFilesSize(),
                        Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                        StpUtil.hasPermission("torrent:bypass_review"),false, category.getId(),
                        promotionPolicy.getId(), form.getDescription(), tags.stream().map(Tag::getId).toList(),0,0,0);
            }
            if (torrent != null) {
                log.info("Saving torrent {} to {}", torrent.getId(), torrentsDirectory);
                torrent = torrentService.save(torrent);
                BigDecimal uploadReward = BigDecimal.valueOf(200);
                user.setScore(user.getScore().add(uploadReward));
                userService.save(user);
            }
            log.info(torrent.toString());
            return ResponseEntity.ok().body(new TorrentUploadSuccessResponseDTO(torrent.getId(), parser.getInfoHash(), form.getFile()));
        } catch (EmptyTorrentFileException e) {
            throw new APIGenericException(INVALID_TORRENT_FILE, "This torrent is empty.");
        } catch (InvalidTorrentVersionException e) {
            throw new APIGenericException(INVALID_TORRENT_FILE, "V2 Torrent are not supported.");
        } catch (TorrentException e) {
            throw new APIGenericException(INVALID_TORRENT_FILE, e.getClass().getSimpleName() + ":" + e.getMessage());
        }
    }

    @PostMapping("/search")
    public TorrentSearchResultResponseDTO search(@RequestBody(required = false) @Nullable SearchTorrentRequestDTO searchRequestDTO) {
        if (searchRequestDTO == null) {
            searchRequestDTO = new SearchTorrentRequestDTO();
        }
        if(searchRequestDTO.getEntriesPerPage()==0){
            searchRequestDTO.setEntriesPerPage(10);
        }
        log.info(searchRequestDTO.toString());
        IPage<Torrent> page = torrentService.search(searchRequestDTO);

        List<TorrentBasicResponseDTO> dtoList = page.getRecords().stream()
                .map(torrent -> {
                    Category category = categoryService.getCategory(torrent.getCategoryId());

                    PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());

                    List<String> tagNames = tagService.getTagNamesByIds(torrent.getTag());

                    return new TorrentBasicResponseDTO(torrent, category, promotionPolicy, tagNames);
                })
                .toList();

        long totalElements = page.getTotal();
        long totalPages = (totalElements + searchRequestDTO.getEntriesPerPage() - 1) / searchRequestDTO.getEntriesPerPage();  // 向上取整


        return new TorrentSearchResultResponseDTO(totalElements, totalPages, dtoList);
    }

    @GetMapping("/view/{info_hash}")
    public TorrentInfoResponseDTO view(@PathVariable("info_hash") String infoHash) {
        Torrent torrent = torrentService.getTorrentByInfoHash(infoHash);
        if (torrent == null) {
            throw new APIGenericException(TORRENT_NOT_EXISTS, "This torrent not registered on this tracker");
        }
        List<String> tagList = tagService.getTagNamesByIds(torrent.getTag());
        User user = userService.getUser(torrent.getUserId());
        Category category = categoryService.getCategory(torrent.getCategoryId());
        PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());
        return new TorrentInfoResponseDTO(torrent, user, category, promotionPolicy, tagList);
    }

    @PostMapping("/scrape")
    public TorrentScrapeResponseDTO scrape(@RequestBody TorrentScrapeRequestDTO scrapeRequestDTO) {
        if (scrapeRequestDTO.getTorrents() == null) {
            throw new APIGenericException(MISSING_PARAMETERS, "You must provide a list of info_hash");
        }
        Map<String, ScrapeContainerDTO> scrapes = new HashMap<>();
        Map<String, List<TransferHistoryDTO>> details = new HashMap<>();
        for (String infoHash : scrapeRequestDTO.getTorrents()) {
            Torrent torrent = torrentService.getTorrentByInfoHash(infoHash);
            if (torrent == null) {
                continue;
            }
            TransferHistoryService.PeerStatus peerStatus = transferHistoryService.getPeerStatus(torrent);
            scrapes.put(infoHash, new ScrapeContainerDTO(peerStatus.downloaded(), peerStatus.complete(), peerStatus.incomplete(), peerStatus.downloaders()));
            details.put(infoHash, transferHistoryService.getTransferHistory(torrent).stream().map(TransferHistoryDTO::new).toList());
        }
        return new TorrentScrapeResponseDTO(scrapes, details);
    }

    @GetMapping("/download/{info_hash}")
    public HttpEntity<?> download(@PathVariable("info_hash") String infoHash, @RequestParam @NotNull Map<String, String> params) throws IOException, TorrentException {
        log.info("Download torrent {}", infoHash);
        User user;
        if (params.containsKey("passkey")) {
            user = authenticationService.authenticate(params.get("passkey"), IPUtil.getRequestIp(request));
        } else {
            user = userService.getUser(StpUtil.getLoginIdAsLong());
        }
        if (user == null) {
            throw new APIGenericException(AUTHENTICATION_FAILED, "Neither passkey or session provided.");
        }
//        long downloaded = user.getDownloaded();
//        long uploaded = user.getUploaded();
//        double ratio = (downloaded == 0) ? Double.POSITIVE_INFINITY : (double) uploaded / downloaded;
//
//        if (ratio < 1) {
//            throw new APIGenericException(RATIO_TOO_LOW, String.format("您的分享率为 %.2f，低于1，无法执行该操作。", ratio));
//        }
        TrackerConfig trackerConfig = settingService.get(TrackerConfig.getConfigKey(), TrackerConfig.class);
        if (StringUtils.isEmpty(infoHash)) {
            throw new APIGenericException(MISSING_PARAMETERS, "You must provide a info_hash.");
        }
        Torrent torrent = torrentService.getTorrentByInfoHash(infoHash);

        if (torrent == null) {
            throw new APIGenericException(TORRENT_NOT_EXISTS, "This torrent not registered on this tracker");
        }
        if (torrent.isUnderReview()) {
            if (!StpUtil.hasPermission(user.getId(), "torrent:download_review")) {
                throw new NotPermissionException("torrent:download_review");
            }
        }
        File torrentFile = new File(torrentsDirectory, infoHash + ".torrent");
        if (!torrentFile.exists()) {
            throw new APIGenericException(TORRENT_FILE_MISSING, "This torrent's file are missing on this tracker, please contact with system administrator.");
        }
        TorrentParser parser = new TorrentParser(Files.readAllBytes(torrentFile.toPath()), false);
        //User user1 = userService.getUser(torrent.getUserId());
        parser.rewriteForUser(trackerConfig.getTrackerURL(), user.getPasskey(), user);
        log.info("userPasskey{}", user.getPasskey());
        String fileName = "[" + trackerConfig.getTorrentPrefix() + "] " + torrent.getTitle() + ".torrent";
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_TYPE, "application/x-bittorrent");
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncodeUtil.urlEncode(fileName, false));
        return new HttpEntity<>(parser.save(), header);
    }

    @GetMapping("/dead-seeds")
    public DeadSeedResponseDTO getDeadSeedTorrents() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<TransferHistory> userHistories = transferHistoryService.getByUserId(userId);
        if (userHistories.isEmpty()) {
            return new DeadSeedResponseDTO("当前没有资源可保种。", List.of());
        }

        Set<Long> torrentIds = userHistories.stream()
                .map(TransferHistory::getTorrentId)
                .collect(Collectors.toSet());

        List<TorrentBasicResponseDTO> deadSeedDTOs = torrentService.getByIds(torrentIds).stream()
                .filter(t -> t.getSeederCount() <4)
                .map(torrent -> {
                    Category category = categoryService.getCategory(torrent.getCategoryId());
                    PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());
                    List<String> tagNames = tagService.getTagNamesByIds(torrent.getTag());

                    return new TorrentBasicResponseDTO(torrent, category, promotionPolicy, tagNames);
                })
                .toList();

        if (deadSeedDTOs.isEmpty()) {
            return new DeadSeedResponseDTO("当前没有资源可保种。", List.of());
        }

        return new DeadSeedResponseDTO("该种子做种人数少，并且您曾拥有资源。做种时做种积分X5直到做种人数>3", deadSeedDTOs);
    }

    // src/main/java/com/example/ptweb/controller/torrent/TorrentController.java
    @PostMapping("/hot")
    public TorrentSearchResultResponseDTO hot(@RequestBody(required = false) @Nullable SearchTorrentRequestDTO searchRequestDTO) {
        if (searchRequestDTO == null) {
            searchRequestDTO = new SearchTorrentRequestDTO();
        }
        if (searchRequestDTO.getEntriesPerPage() == 0) {
            searchRequestDTO.setEntriesPerPage(10);
        }
        IPage<Torrent> page = torrentService.searchHot(searchRequestDTO);

        List<TorrentBasicResponseDTO> dtoList = page.getRecords().stream()
                .map(torrent -> {
                    Category category = categoryService.getCategory(torrent.getCategoryId());
                    PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());
                    List<String> tagNames = tagService.getTagNamesByIds(torrent.getTag());
                    return new TorrentBasicResponseDTO(torrent, category, promotionPolicy, tagNames);
                })
                .toList();

        long totalElements = page.getTotal();
        long totalPages = (totalElements + searchRequestDTO.getEntriesPerPage() - 1) / searchRequestDTO.getEntriesPerPage();

        return new TorrentSearchResultResponseDTO(totalElements, totalPages, dtoList);
    }


}
