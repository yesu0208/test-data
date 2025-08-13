package arile.toy.test_data.repository;

import arile.toy.test_data.domain.TableSchema;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableSchemaRepository extends JpaRepository<TableSchema, Long> {
    Page<TableSchema> findByUserId(String userId, Pageable pageable); // List가 아닌 Page인 경우에는 Pageable 하나 더 추가
    Optional<TableSchema> findByUserIdAndSchemaName(String userId, String schemaName);
    void deleteByUserIdAndSchemaName(String userId, String schemaName);
}
