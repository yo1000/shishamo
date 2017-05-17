package red.sukun1899.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import red.sukun1899.model.Table;

import java.util.List;
import java.util.Map;

/**
 * @author su-kun1899
 */
@Repository
@Mapper
public interface TableRepository {
  List<Table> selectAll(String schemaName);

  Table select(@Param("schemaName") String schemaName, @Param("name") String name);

  Map<String, Long> selectParentTableCountsByTableName(String schemaName);
}