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

        String sql = "CREATE TABLE `t_candidate_recruit` (\n" +
                "  `candidateId` int(11) NOT NULL DEFAULT '0' COMMENT '应聘者id',\n" +
                "  `channelType` tinyint(4) NOT NULL DEFAULT '0' COMMENT '渠道类型：校招1、社招2、特批3',\n" +
                "  `channelId` int(11) NOT NULL DEFAULT '0' COMMENT '招聘渠道id',\n" +
                "  `inviteLjCode` int(11) NOT NULL DEFAULT '0' COMMENT '邀约人系统号',\n" +
                "  `resumeLjCode` int(11) NOT NULL DEFAULT '0' COMMENT '简历人系统号',\n" +
                "  `restorationApply` varchar(100) NOT NULL DEFAULT '' COMMENT '复职申请单：图片地址',\n" +
                "  `qualificationInquiry` varchar(100) NOT NULL DEFAULT '' COMMENT '资格查询截图：图片地址',\n" +
                "  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`candidateId`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应聘详情信息';";

        generateDomain(sql, "t_", "", ".");

    }
}
