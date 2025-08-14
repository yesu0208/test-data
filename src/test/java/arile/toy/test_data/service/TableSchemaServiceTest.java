package arile.toy.test_data.service;

import arile.toy.test_data.domain.TableSchema;
import arile.toy.test_data.dto.TableSchemaDto;
import arile.toy.test_data.repository.TableSchemaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] 테이블 스키마 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class TableSchemaServiceTest {

    @InjectMocks private TableSchemaService sut; // 테스트 대상 (주입하는 대상)

    @Mock private TableSchemaRepository tableSchemaRepository; // 이것을 Mock으로 만들어 TableSchemaService에 주입하고 싶다.

    @DisplayName("사용자 ID가 주어지면, 테이블 스키마 목록을 반환한다.")
    @Test // page가 아닌 list로 반환되는 것만 test (이것만 쓸 것 같으니..)
    void givenUserId_whenLoadingMySchemas_thenReturnsTableSchemaList() {
        // Given
        String userId = "userId";
        given(tableSchemaRepository.findByUserId(userId, Pageable.unpaged())).willReturn(new PageImpl<>(List.of(
                TableSchema.of("table1", userId),
                TableSchema.of("table2", userId),
                TableSchema.of("table3", userId)
        ))); // list를 page로 감싸기 위해 PageImpl 사용

        // When
        List<TableSchemaDto> result = sut.loadMySchemas(userId);

        // Then
        assertThat(result)
                .hasSize(3)
                .extracting("schemaName")
                .containsExactly("table1", "table2", "table3");
        then(tableSchemaRepository).should().findByUserId(userId, Pageable.unpaged()); // page 안할 것이므로 unpaged
    }

    @DisplayName("사용자 ID와 스키마 이름이 주어지면, 테이블 스키마를 반환한다.")
    @Test
    void givenUserIdAndSchemaName_whenLoadingMySchema_thenReturnsTableSchema() {
        // Given
        String userId = "userId";
        String schemaName = "table1";
        TableSchema tableSchema = TableSchema.of(schemaName, userId);
        given(tableSchemaRepository.findByUserIdAndSchemaName(userId, schemaName)).willReturn(Optional.of(tableSchema));

        // When
        TableSchemaDto result = sut.loadMySchema(userId, schemaName);

        // Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("schemaName", schemaName)
                .hasFieldOrPropertyWithValue("userId", userId);
        then(tableSchemaRepository).should().findByUserIdAndSchemaName(userId, schemaName);
    }

    @DisplayName("사용자 ID와 스키마 이름에 대응하는 테이블 스키마가 없으면, 예외를 던진다.")
    @Test
    void givenUserIdAndSchemaName_whenLoadingNonexistentMySchema_thenThrowsException() {
        // Given
        String userId = "userId";
        String schemaName = "table1";
        given(tableSchemaRepository.findByUserIdAndSchemaName(userId, schemaName)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.loadMySchema(userId, schemaName));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("테이블 스키마가 없습니다 - userId: " + userId + ", schemaName: " + schemaName);
        then(tableSchemaRepository).should().findByUserIdAndSchemaName(userId, schemaName);
    }

}