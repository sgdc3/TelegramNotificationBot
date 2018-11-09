package com.github.sgdc3.telegramnotificationbot.storage;

import org.springframework.data.repository.CrudRepository;

public interface GroupDataRepository extends CrudRepository<GroupData, Long> {

    default GroupData findByIdOrNew(Long id) {
        return findById(id).orElseGet(() -> {
            GroupData data = new GroupData(id);
            save(data);
            return data;
        });
    }
}
