package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ptweb.controller.torrent.dto.request.SearchTorrentRequestDTO;
import com.example.ptweb.entity.Category;
import com.example.ptweb.entity.PromotionPolicy;
import com.example.ptweb.entity.Tag;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.mapper.TorrentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TorrentService {

    @Autowired
    private TorrentMapper torrentMapper;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PromotionService promotionService;
    @Autowired
    private TagService tagService;

    public IPage<Torrent> search(SearchTorrentRequestDTO dto) {
        int page = Math.max(dto.getPage(), 1) ;
        int size = dto.getEntriesPerPage();
        List<String> category = Optional.ofNullable(dto.getCategory()).orElse(Collections.emptyList());
        List<String> promotion = Optional.ofNullable(dto.getPromotion()).orElse(Collections.emptyList());
        List<String> tags = Optional.ofNullable(dto.getTag()).orElse(Collections.emptyList());
        return search(
                dto.getKeyword(),
                category,
                promotion,
                tags,
                new Page<>(page, size)
        );
    }

    public IPage<Torrent> search(String keyword, List<String> categorySlugs, List<String> promotionSlugs, List<String> tagNames, IPage<Torrent> page) {
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

        if (!tagNames.isEmpty()) {
            List<Long> tagIds = tagNames.stream()
                    .map(name -> {
                        Tag tag = tagService.getTag(name);
                        return tag == null ? null : tag.getId();
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (!tagIds.isEmpty()) {
                wrapper.and(w -> {
                    for (Long tagId : tagIds) {
                        w.or().apply("JSON_CONTAINS(tag, JSON_ARRAY({0}))", tagId);
                    }
                });
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
    public List<Torrent> getByIds(Set<Long> ids) {
        return torrentMapper.selectBatchIds(ids);
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

    public List<Torrent> getAllTorrents() {
        return torrentMapper.selectList(null);
    }


    public void saveAll(List<Torrent> torrents) {
        for (Torrent torrent : torrents) {
            save(torrent);
        }
    }


}
