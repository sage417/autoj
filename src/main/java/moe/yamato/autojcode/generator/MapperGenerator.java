package moe.yamato.autojcode.generator;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import moe.yamato.autojcode.domain.Property;
import moe.yamato.autojcode.utils.FreemarkerUtil;
import moe.yamato.autojcode.utils.SqlUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Package: moe.yamato.autojcode
 * Author: mac
 * Date: 2017/11/16
 */
public class MapperGenerator {


    public static final Set<String> COLUMNS_NEEDNT_INSERT = Sets.newHashSet("id", "ctime", "mtime");
    public static final Set<String> COLUMNS_NEEDNT_UPDATE = Sets.newHashSet("id", "cuser", "ctime", "mtime");

    public static void generateMapper(
            String tableName,
            String mapperPackage,
            Set<String> columns
    ) throws IOException, TemplateException {

        Map<String, Object> dataModel = Maps.newHashMap();
        dataModel.put("resultType", "");
        dataModel.put("mapperNameSpace", mapperPackage + ".");
        dataModel.put("tableName", tableName);
        dataModel.put("columns", columns);
        dataModel.put("insertColumns", Sets.difference(columns, COLUMNS_NEEDNT_INSERT));
        dataModel.put("updateColumns", Sets.difference(columns, COLUMNS_NEEDNT_UPDATE));

        final Template template = FreemarkerUtil.getTemplate("mapper.xml.ftl");

        final StringWriter stringWriter = new StringWriter(1000);
        template.process(dataModel, stringWriter);

        System.out.println(stringWriter.toString());
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

        final Set<Property> columns = SqlUtils.getProperties(sql);

        generateMapper(SqlUtils.getTableDescriber(sql).getTableName(), "", Sets.newLinkedHashSet(columns.stream().map(Property::getName).collect(Collectors.toList())));
    }
}
