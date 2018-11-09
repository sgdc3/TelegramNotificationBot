package com.github.sgdc3.telegramnotificationbot.storage;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
@Entity
public class TwitchChannel {

    @Id
    @Column
    @NonNull
    private Long id;

    @Setter
    @Column
    private Long lastStream;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<GroupData> groups;
}
