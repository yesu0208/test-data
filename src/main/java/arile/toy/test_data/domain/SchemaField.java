package arile.toy.test_data.domain;

import arile.toy.test_data.domain.constant.MockDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;


/**
 * 특정 {@link TableSchema}의 단위 필드 정보.
 * 이 필드들이 모여서 테이블 스키마를 구성한다.
 *
 * @author Arile
 */


@Getter
@ToString(callSuper = true)
@Entity
public class SchemaField extends AuditingFields{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private TableSchema tableSchema;

    @Setter @Column(nullable = false) private String fieldName;
    @Setter @Column(nullable = false) @Enumerated(EnumType.STRING) private MockDataType mockDataType;
    @Setter @Column(nullable = false) private Integer fieldOrder;
    @Setter @Column(nullable = false) private Integer blankPercent;

    @Setter private String typeOptionJson; // 이것은 없을 수도 있다. (Option)
    @Setter private String forceValue; // 값을 랜던생성 안하고 수기로 직접 넣을 것인가? 없을 수도 있다.


    protected SchemaField() {}

    public SchemaField(String fieldName, MockDataType mockDataType,
                       Integer fieldOrder, Integer blankPercent, String typeOptionJson, String forceValue){
        this.fieldName = fieldName;
        this.mockDataType = mockDataType;
        this.fieldOrder = fieldOrder;
        this.blankPercent = blankPercent;
        this.typeOptionJson = typeOptionJson;
        this.forceValue = forceValue;
    }

    public static SchemaField of(String fieldName, MockDataType mockDataType,
                                 Integer fieldOrder, Integer blankPercent, String typeOptionJson, String forceValue) {
        return new SchemaField(fieldName, mockDataType, fieldOrder, blankPercent, typeOptionJson, forceValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchemaField that)) return false;

        if (getId() == null) {
            return Objects.equals(this.getFieldName(), that.getFieldName()) &&
                    Objects.equals(this.getMockDataType(), that.getMockDataType()) &&
                    Objects.equals(this.getFieldOrder(), that.getFieldOrder()) &&
                    Objects.equals(this.getBlankPercent(), that.getBlankPercent()) &&
                    Objects.equals(this.getTypeOptionJson(), that.getTypeOptionJson()) &&
                    Objects.equals(this.getForceValue(), that.getForceValue());
        }
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null){
            return Objects.hash(getFieldName(), getMockDataType(), getFieldOrder(), getBlankPercent(), getTypeOptionJson(), getForceValue());
        }
        return Objects.hash(getId());
    }
}
