package arile.toy.test_data.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;


@Getter
public enum MockDataType {
    // base type
    STRING(Set.of("minLength", "maxLength", "pattern"), null),
    NUMBER(Set.of("min", "max", "decimals"), null),
    BOOLEAN(Set.of(), null),
    DATETIME(Set.of("from", "to"), null),
    ENUM(Set.of("elements"), null),

    // 사람이 이해할 수 있는 type
    SENTENCE(Set.of("minSentences", "maxSentences"), STRING),
    PARAGRAPH(Set.of("minParagraphs", "maxParagraphs"), STRING),
    UUID(Set.of(), STRING),
    EMAIL(Set.of(), STRING),
    CAR(Set.of(), STRING),
    ROW_NUMBER(Set.of("start, step"), NUMBER),
    NAME(Set.of(), STRING)
    ;

    private final Set<String> requiredOptions;
    private final MockDataType baseType;

    MockDataType(Set<String> requiredOptions, MockDataType baseType) {
        this.requiredOptions = requiredOptions;
        this.baseType = baseType;
    }

    public boolean isBaseType() {
        return baseType == null;
    }

    public MockDataTypeObject toObject(){
        return new MockDataTypeObject(
                this.name(),
                this.requiredOptions,
                this.baseType == null ? null : this.baseType.name()
                );
    }

    public record MockDataTypeObject(
            String name,
            Set<String> requiredOptions,
            String baseType){
    }

}
