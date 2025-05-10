package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ptweb.controller.torrent.dto.request.SearchTorrentRequestDTO;
import com.example.ptweb.entity.Category;
import com.example.ptweb.entity.PromotionPolicy;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.entity.TransferHistory;
import com.example.ptweb.mapper.TorrentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TorrentService {

    @Autowired
    private TorrentMapper torrentMapper;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PromotionService promotionService;

    public IPage<Torrent> search(SearchTorrentRequestDTO dto) {
        int page = Math.max(dto.getPage(), 0) + 1;
        int size = Math.min(Math.max(dto.getEntriesPerPage(), 1), 300);

        return search(
                dto.getKeyword(),
                dto.getCategory(),
                dto.getPromotion(),
                dto.getTag(),
                new Page<>(page, size)
        );
    }

    public IPage<Torrent> search(String keyword, List<String> categorySlugs, List<String> promotionSlugs, List<String> tagIds, IPage<Torrent> page) {
        LambdaQueryWrapper<Torrent> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Torrent::getTitle, keyword)
                    .or()
                    .like(Torrent::getSubTitle, keyword));
        }

        if (!categorySlugs.isEmpty()) {
            List<Long> categoryIds = categorySlugs.stream()
                    .map(slug -> {
                        Category c = categoryService.getCategory(slug);
                        return c == null ? null : c.getId();
                    })
                    .filter(Objects::nonNull)
                    .toList();
            if (!categoryIds.isEmpty()) {
                wrapper.in(Torrent::getCategoryId, categoryIds);
            }
        }

        if (!promotionSlugs.isEmpty()) {
            List<Long> policyIds = promotionSlugs.stream()
                    .map(slug -> {
                        PromotionPolicy p = promotionService.getPromotionPolicy(slug);
                        return p == null ? null : p.getId();
                    })
                    .filter(Objects::nonNull)
                    .toList();
            if (!policyIds.isEmpty()) {
                wrapper.in(Torrent::getPromotionPolicyId, policyIds);
            }
        }

        if (!tagIds.isEmpty()) {
            for (String tagId : tagIds) {
                wrapper.like(Torrent::getTag, tagId);  // 模糊匹配字符串中的 tagId
            }
        }

        wrapper.orderByDesc(Torrent::getId);

        return torrentMapper.selectPage(page, wrapper);
    }
    public Torrent save(Torrent torrent) {
        if (torrent.getId() == null) {
            torrentMapper.insert(torrent);
        } else {
            torrentMapper.updateById(torrent);
        }
        return torrent;
    }

    public Torrent getTorrentById(Long id) {
        return torrentMapper.selectById(id);
    }

    public Torrent getTorrentByInfoHash(String infoHash) {
        return torrentMapper.selectOne(new LambdaQueryWrapper<Torrent>()
                .eq(Torrent::getInfoHash, infoHash.toLowerCase()));
    }

    public List<Torrent> getTorrentsByUserId(Long userId) {
        return torrentMapper.selectList(new LambdaQueryWrapper<Torrent>()
                .eq(Torrent::getUserId, userId)
                .orderByDesc(Torrent::getId));
    }

}
