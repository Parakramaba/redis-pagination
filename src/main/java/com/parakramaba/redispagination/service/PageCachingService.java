package com.parakramaba.redispagination.service;

import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.exception.ResourceNotFoundException;
import com.parakramaba.redispagination.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("PageCachingService")
public class PageCachingService {

    // INJECT REPOSITORY OBJECT DEPENDENCIES
    @Autowired
    private PersonRepository personRepository;

    // INJECT REDIS DEPENDENCIES
    @Autowired
    private RedisTemplate<String, Page<Person>> redisPersonPageTemplate;

    /**
     * This method creates cache entries of adjacent five pages,
     * and returns requested page of persons for getAllPersons API.
     * @param requestedPageNumber Page number, not null
     * @param requestedPageSize Number of elements for the page, not null
     * @param requestedSortingField Sorting field, not null
     * @param requestedPageKey Cache key of the requested page, not null
     * @return Page of persons
     */
    protected Page<Person> cachingAndGetAllPersonsPage(final int requestedPageNumber,
                                                       final int requestedPageSize,
                                                       final String requestedSortingField,
                                                       final String requestedPageKey) {

        int nextPageNumber = requestedPageNumber + 1;

        // Get and cache the requested page
        Page<Person> requestedPage = getAndCacheRequestedAllPersonsPage(requestedPageNumber, requestedPageSize,
                requestedSortingField, requestedPageKey);

        // Set adjacent pages of requested page into cache
        setAdjacentPagesForAllPersons(nextPageNumber, requestedPageSize, requestedSortingField);

        return requestedPage;
    }

    /**
     * This method gets the requested person page from the database,
     * make a cache entry with it and return the requested page for getAllPersons API.
     * @param requestedPageNumber Page number, not null
     * @param requestedPageSize Number of elements for the page, not null
     * @param requestedSortingField Sorting field, not null
     * @param requestedPageKey Cache key of the requested page, not null
     * @return Requested page of persons
     * @throws ResourceNotFoundException When the requested Person page not found
     */
    protected Page<Person> getAndCacheRequestedAllPersonsPage(final int requestedPageNumber,
                                                              final int requestedPageSize,
                                                              final String requestedSortingField,
                                                              final String requestedPageKey)
            throws ResourceNotFoundException {

        // Get the requested page from database and set it into the cache
        Page<Person> requestedPage = personRepository.findAll(
                PageRequest.of(
                        requestedPageNumber,
                        requestedPageSize,
                        Sort.Direction.ASC, requestedSortingField
                )
        );
        if (requestedPage.getContent().isEmpty()) {
            throw new ResourceNotFoundException("There are no persons found");
        } else {
            redisPersonPageTemplate.opsForValue().setIfAbsent(requestedPageKey, requestedPage,
                    10, TimeUnit.MINUTES);
        }

        return requestedPage;
    }

    /**
     * This method creates cache entries for four adjacent pages of requested page for getAllPersons API.
     * @param pageNumber Page number, not null
     * @param requestedPageSize Number of elements for the page, not null
     * @param requestedSortingField Sorting filed, not null
     */
    protected void setAdjacentPagesForAllPersons(final int pageNumber,
                                                 final int requestedPageSize,
                                                 final String requestedSortingField) {

        int nextPageNumber = pageNumber;

        // Get data of next four adjacent pages and store them in the cache
        loopCachingAdjacentPages:
        for (int i = 0; i < 4; i++) {
            String pageKey = "allPersons:" + nextPageNumber + ":" + requestedPageSize
                    + ":" + requestedSortingField;
            Page<Person> adjacentPage = redisPersonPageTemplate.opsForValue().get(pageKey);
            if (adjacentPage == null) {
                Page<Person> allPersonsPage = personRepository.findAll(
                        PageRequest.of(
                                nextPageNumber,
                                requestedPageSize,
                                Sort.Direction.ASC, requestedSortingField
                        )
                );

                if (allPersonsPage.getContent().isEmpty()) {
                    break loopCachingAdjacentPages;
                } else {
                    redisPersonPageTemplate.opsForValue().setIfAbsent(pageKey, allPersonsPage,
                            10, TimeUnit.MINUTES);
                }
            }
            nextPageNumber++;
        }
    }
}
