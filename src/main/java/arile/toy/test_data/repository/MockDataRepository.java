package arile.toy.test_data.repository;

import arile.toy.test_data.domain.MockData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockDataRepository extends JpaRepository<MockData, Long> {
}
