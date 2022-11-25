package net.vash.awss3springrestapi.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Entity
@Table(name = "events")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type",nullable = false)
    private EventType eventType = EventType.CREATED;
    @CreationTimestamp
    @PastOrPresent
    @Column(name = "created",nullable = false)
    private Date created = new Date();

    @ManyToOne(
            targetEntity = User.class,
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            table = "events",
            name = "user_id",
            nullable = false,
            updatable = false
    )
    @ToString.Exclude
    private User user;

    @OneToOne(
            targetEntity = File.class,
            cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            table = "events",
            name = "file_id",
            nullable = false,
            updatable = false
    )
    private File file;

}
