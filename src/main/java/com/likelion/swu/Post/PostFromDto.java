package com.likelion.swu.Post;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostFromDto {

    private Long id;
    @NotNull(message = "제목은 필수 값 입니다.")
    private String title;
    private String body;
    private RequestStatus request;
}
