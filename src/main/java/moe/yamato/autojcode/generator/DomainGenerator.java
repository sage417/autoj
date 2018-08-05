package moe.yamato.autojcode.generator;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import freemarker.template.TemplateException;
import moe.yamato.autojcode.domain.Property;
import moe.yamato.autojcode.domain.TableDescriber;
import moe.yamato.autojcode.utils.FreemarkerUtil;
import moe.yamato.autojcode.utils.SqlUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class DomainGenerator {

    public static void generateDomain(String sql, String tableNamePrefix, String packageName, String output) throws IOException, TemplateException {
        Map<String, Object> dataModel = Maps.newHashMap();
        dataModel.put("packageName", packageName);
        TableDescriber tableDescriber = SqlUtils.getTableDescriber(sql);
        dataModel.put("classComment", tableDescriber.getComment());
        String className = getClassName(tableDescriber.getTableName(), tableNamePrefix);
        dataModel.put("className", className);
        Set<Property> properties = SqlUtils.getProperties(sql);
        dataModel.put("attrs", properties);

        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(
                Paths.get(output, className + ".java"),
                Charset.forName("utf-8"),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            FreemarkerUtil.getTemplate("bean.ftl").process(dataModel, bufferedWriter);
        }
    }

    private static String getClassName(String tableName, String tableNamePrefix) {

        String tmp = tableName;

        if (tableNamePrefix.equals(Strings.commonPrefix(tableName, tableNamePrefix))) {
            tmp = tableName.substring(tableNamePrefix.length());
        }

        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(tmp);
    }

    public static void main(String[] args) throws IOException, TemplateException {

        String sql = "CREATE TABLE `t_wx_bridge_person_entry` (\n" +
                "  `offerId` bigint(20) NOT NULL DEFAULT '0' COMMENT 'offer主键',\n" +
                "  `practice` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否实习 0 默认 1实习 2不实习',\n" +
                "  `entryStartDate` datetime NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT '预计入职开始时间',\n" +
                "  `entryEndDate` datetime NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT '预计入职结束时间',\n" +
                "  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`offerId`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='北森校招实习入职信息表';";

        generateDomain(sql, "t_wx", "com.lianjia.confucius.stanford.entity", ".");

    }
}
