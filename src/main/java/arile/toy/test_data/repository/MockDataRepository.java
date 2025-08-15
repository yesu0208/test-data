package arile.toy.test_data.repository;

import arile.toy.test_data.domain.MockData;
import arile.toy.test_data.domain.constant.MockDataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockDataRepository extends JpaRepository<MockData, Long> {
    List<MockData> findByMockDataType(MockDataType mockDataType);
}
