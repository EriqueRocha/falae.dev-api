package dev.falae.application.usecases;

import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.SearchContentType;
import dev.falae.application.ports.dto.TagSearchPageResponse;
import dev.falae.application.ports.repositories.SearchRepository;

import java.util.List;

public class SearchByTagsUseCase {

    private final SearchRepository searchRepository;

    public SearchByTagsUseCase(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public TagSearchPageResponse search(
            List<String> tags,
            SearchContentType type,
            FeedSortType sortType,
            int page,
            int size
    ) {
        return searchRepository.searchByTags(tags, type, sortType, page, size);
    }
}
