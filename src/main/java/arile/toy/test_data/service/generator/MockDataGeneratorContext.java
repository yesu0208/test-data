package arile.toy.test_data.service.generator;

import arile.toy.test_data.domain.constant.MockDataType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MockDataGeneratorContext {

    private final Map<MockDataType, MockDataGenerator> mockDataGeneratorMap;

    public MockDataGeneratorContext(List<MockDataGenerator> mockDataGenerators) {
        this.mockDataGeneratorMap = mockDataGenerators.stream()
                .collect(Collectors.toMap(MockDataGenerator::getType, Function.identity()));
    }

    public String generate(MockDataType mockDataType, Integer blankPercent, String typeOptionJson, String forceValue){
        MockDataGenerator generator = mockDataGeneratorMap.get(mockDataType);

        if (generator == null) {
            generator = mockDataGeneratorMap.get(MockDataType.STRING);
        }

        return generator.generate(blankPercent, typeOptionJson, forceValue);
    }
}
