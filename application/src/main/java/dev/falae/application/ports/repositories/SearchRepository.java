package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.SearchContentType;
import dev.falae.application.ports.dto.TagSearchPageResponse;

import java.util.List;

public interface SearchRepository {
    TagSearchPageResponse searchByTags(
            List<String> tags,
            SearchContentType type,
            FeedSortType sortType,
            int page,
            int size
    );
}
