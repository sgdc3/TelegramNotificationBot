package com.github.sgdc3.telegramnotificationbot.storage;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
@Entity
public class YouTubeChannel {

    @Id
    @Column(length = 32)
    @NonNull
    private String id;

    @Setter
    @Column(length = 16)
    private String lastVideo;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<GroupData> groups;
}
