package arile.toy.test_data.service.exporter;

import arile.toy.test_data.dto.SchemaFieldDto;
import arile.toy.test_data.dto.TableSchemaDto;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class DelimiterBasedFileExporter implements MockDataFileExporter {

    /**
     * 파일 열 구분자로 사용할 문자열을 반환한다.
     *
     * @return 파일 열 구분자
     */
    public abstract String getDelimiter();


    @Override
    public String export(TableSchemaDto dto, Integer rowCount) {
        StringBuilder sb = new StringBuilder(); // 입력을 받아 CSV 형식으로 재조립

        // 해더 만들기 (CSV 파일 만들 때 첫번째 줄에다가 제목 붙이기)
        sb.append(dto.schemaFields().stream()
                .sorted(Comparator.comparing(SchemaFieldDto::fieldOrder)) // filedOrder를 기준으로 정렬
                .map(SchemaFieldDto::fieldName)
                .collect(Collectors.joining(getDelimiter())) // 필드 이름을 어떤 구분자로 join할 것인가?
        );
        sb.append("\n"); // 줄바꿈

        // 데이터 부분 (rowCount만큼 순회를 돈다)
        IntStream.range(0, rowCount).forEach(i -> {
            sb.append(dto.schemaFields().stream()
                    .sorted(Comparator.comparing(SchemaFieldDto::fieldOrder)) // filedOrder를 기준으로 정렬
                    .map(field -> "가짜-데이터")
                    // 가짜 데이터는 null을 포함할 수 있음 (blankPercent) : CSV 에서는 비어있는 값을 출력하는 방식이 null이 아님.
                    // 아무것도 안써주는 형태. 그래서 null을 빈 문자열로 변환
                    .map(v -> v == null ? "" : v)
                    .collect(Collectors.joining(getDelimiter()))
            );
            sb.append("\n"); // 줄바꿈
        });

        return sb.toString();
    }
}
