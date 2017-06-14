package culturelog.rest.repository;

import culturelog.rest.domain.Location;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Jan Venstermans
 */
public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findByUserId(String userId);
}
