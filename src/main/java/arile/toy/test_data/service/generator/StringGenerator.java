package arile.toy.test_data.service.generator;

import arile.toy.test_data.domain.constant.MockDataType;
import arile.toy.test_data.dto.MockDataDto;
import arile.toy.test_data.repository.MockDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

@Slf4j // 로그 찍기 위해
@RequiredArgsConstructor
@Transactional(readOnly = true) // save는 하지 않을 예정이므로
@Component
public class StringGenerator implements MockDataGenerator{

    private final MockDataRepository mockDataRepository; // repository가 등장 -> transactional 추가
    private final ObjectMapper mapper; // Option 값을 JSON으로 읽어들이므로 필요

    @Override
    public MockDataType getType() {
        return MockDataType.STRING;
    }

    @Override
    public String generate(Integer blankPercent, String typeOptionJson, String forceValue) {

        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        if (randomGenerator.nextInt(100) < blankPercent){
            return null;
        }

        if (forceValue != null && !forceValue.isBlank()) {
            return forceValue;
        }

        Option option = new Option(1, 10); // 기본 옵션
        try { // 실제 받아오는 옵션
            if (typeOptionJson != null && !typeOptionJson.isBlank()) {
                option = mapper.readValue(typeOptionJson, Option.class); // typeOptionJson은 null이 아니어야 한다
            }
        } catch (JsonProcessingException e){
            // warn : 프로그램 중단 사항까지는 아님, error : 프로그램 중단 사항임
            log.warn("Json 옵션 정보를 읽어들이는데 실패하였습니다. 기본 옵션으로 동작합니다 - 입력 옵션: {}, 필요 옵션 예: {}",typeOptionJson, option);
        }

        if (option.minLength() < 1) {
            throw new IllegalArgumentException("[가짜 데이터 생성 옵션 오류] 최소 길이가 1보다 작습니다 - option: " + typeOptionJson);
        } else if (option.maxLength() > 10) {
            throw new IllegalArgumentException("[가짜 데이터 생성 옵션 오류] 최대 길이가 10보다 큽니다 - option: " + typeOptionJson);
        } else if (option.minLength() > option.maxLength()) {
            throw new IllegalArgumentException("[가짜 데이터 생성 옵션 오류] 최소 길이가 최대 길이보다 큽니다 - option: " + typeOptionJson);
        }

        List<MockDataDto> mockDataDtos = mockDataRepository.findByMockDataType(getType())
                .stream().map(MockDataDto::fromEntity).toList();
        // 여기에서 문자열 추출
        String body = mockDataDtos.stream()
                .map(MockDataDto::mockDataValue)
                .collect(Collectors.joining(""))
                .replaceAll("[^가-힣]", ""); // 한글이 아닌 문자열을 잡아서 ""로 치환 : 정규표현식

        // 문자열을 랜덤으로 뽑음 (option : 길이를 이용해서)
        int difference = option.maxLength() - option.minLength();
        int point = randomGenerator.nextInt(body.length() - option.maxLength);
        int offset = (difference < 1 ? difference : randomGenerator.nextInt(Math.max(1, difference))) + option.minLength();

        return body.substring(point, point + offset);
    }

    // "minLength", "maxLength"
    public record Option(Integer minLength, Integer maxLength) {}
}
