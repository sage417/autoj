package moe.yamato.autojcode.generator;

import com.google.common.collect.Maps;
import freemarker.template.TemplateException;
import moe.yamato.autojcode.domain.Property;
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

/**
 * Package: moe.yamato.autojcode.generator
 *
 * @author: 175405@lianjia.com
 * Date: 2018/1/30
 */
public class SpiGenerator {


    private static void generateSpi(String sql) throws IOException, TemplateException {

        Set<Property> properties = SqlUtils.getProperties(sql);

        Map<String, Object> dataModel = Maps.newHashMap();
        dataModel.put("attrs", properties);


        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(
                Paths.get(".", "test" + ".java"),
                Charset.forName("utf-8"),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            FreemarkerUtil.getTemplate("spi.ftl").process(dataModel, bufferedWriter);
        }


    }


    public static void main(String[] args) throws IOException, TemplateException {
        generateSpi("CREATE TABLE `t_plan_attendance_time` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',\n" +
                            "  `planId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '外键-实施计划id',\n" +
                            "  `beginTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到开始时间',\n" +
                            "  `endTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到结束时间',\n" +
                            "  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                            "  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                            "  `cuser` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建人',\n" +
                            "  `muser` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '修改人',\n" +
                            "  `status` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '状态(0为已删除,1为可用)',\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='签到计划表'");
    }
}
