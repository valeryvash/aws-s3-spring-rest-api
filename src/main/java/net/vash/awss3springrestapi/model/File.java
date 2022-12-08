package net.vash.awss3springrestapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @OneToOne(
            mappedBy = "file",
            cascade = {CascadeType.PERSIST},
            optional = false
    )
    private Event event = new Event();

    @PrePersist
    public void prePersist() {
        if (this.event != null && (this.event.getFile() == null || this.event.getFile() != this)) {
            this.event.setFile(this);
        }
    }
}
