package red.sukun1899.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import red.sukun1899.AppConfig;
import red.sukun1899.model.ReferencedTableCount;
import red.sukun1899.model.Table;
import red.sukun1899.repository.TableRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author su-kun1899
 */
@Service
public class TableService {
  private final AppConfig appConfig;
  private final TableRepository tableRepository;

  public TableService(AppConfig appConfig, TableRepository tableRepository) {
    this.appConfig = appConfig;
    this.tableRepository = tableRepository;
  }

  @Transactional(readOnly = true)
  public List<Table> get() {
    return tableRepository.selectAll(appConfig.getSchemaName());
  }

  @Transactional(readOnly = true)
  public Table get(String tableName) {
    return tableRepository.select(appConfig.getSchemaName(), tableName);
  }

  /**
   * @return Key: tableName, Value: Parent table's count
   */
  public Map<String, Long> getParentTableCountsByTableName() {
    Map<String, ReferencedTableCount> referencedTableCountMap =
        tableRepository.selectParentTableCountsByTableName(appConfig.getSchemaName());

    return referencedTableCountMap.entrySet().stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getCount())
        );
  }
}
