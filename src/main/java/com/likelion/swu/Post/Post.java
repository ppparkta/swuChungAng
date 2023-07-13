package com.likelion.swu.Post;
import com.likelion.swu.User.Account;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name ="post")
public class Post {

    @Id
    @Column(name ="post_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String body;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Building building;
    @Enumerated(EnumType.STRING)
    private RequestStatus request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "created_by_user_id") // 작성자 식별자(ID) 속성 추가
    private String createdByUserId;


    public void updatePost(PostFromDto postFromDto) {
        this.title = postFromDto.getTitle();
        this.body = postFromDto.getBody();
        this.date = LocalDateTime.now(); // 현재 시간으로 설정
        //this.building = postFromDto.getBuilding();
        //this.request = postFromDto.getRequest();
    }
}
