package net.vash.awss3springrestapi.repository;

import net.vash.awss3springrestapi.model.File;
import org.springframework.data.repository.CrudRepository;

public interface FileRepo extends CrudRepository<File,Long> {

}
