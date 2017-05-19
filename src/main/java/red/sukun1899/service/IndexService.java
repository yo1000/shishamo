package red.sukun1899.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import red.sukun1899.AppConfig;
import red.sukun1899.model.Index;
import red.sukun1899.repository.IndexRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author su-kun1899
 */
@Service
public class IndexService {
    private AppConfig appConfig;
    private IndexRepository indexRepository;

    public IndexService(AppConfig appConfig, IndexRepository indexRepository) {
        this.appConfig = appConfig;
        this.indexRepository = indexRepository;
    }

    @Transactional(readOnly = true)
    public List<Index> get(String tableName) {
        return indexRepository.selectByTableName(appConfig.getSchemaName(), tableName).stream()
                .sorted((index1, index2) -> {
                    if (index1.getCategory().order() - index2.getCategory().order() != 0) {
                        return index1.getCategory().order() - index2.getCategory().order();
                    }
                    return index1.getName().compareTo(index2.getName());
                })
                .collect(Collectors.toList());
    }
}
