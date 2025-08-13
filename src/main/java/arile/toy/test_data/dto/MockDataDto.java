package arile.toy.test_data.dto;

import arile.toy.test_data.domain.MockData;
import arile.toy.test_data.domain.SchemaField;
import arile.toy.test_data.domain.constant.MockDataType;

public record MockDataDto(
        Long id,
        MockDataType mockDataType,
        String mockDataValue
) {
    public static MockDataDto fromEntity(MockData entity) {
        return new MockDataDto(
                entity.getId(),
                entity.getMockDataType(),
                entity.getMockDataValue());
    }

    public MockData createEntity() {
        return MockData.of( // id는 자동 생성 - 넣을 필요 x
                this.mockDataType,
                this.mockDataValue);
    }
}
