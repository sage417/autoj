package moe.yamato.autojcode.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import moe.yamato.autojcode.domain.Property;
import moe.yamato.autojcode.domain.TableDescriber;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Package: moe.yamato.autojcode
 * Author: mac
 * Date: 2017/11/21
 */
public abstract class SqlUtils {

    private static final Pattern TABLE_COMMENT_PATTERN =
            Pattern.compile("^CREATE TABLE\\s+`?(\\w+)`?[\\S\\s]*\\)(?:.*?COMMENT\\s*=\\s*'([\\S]*?)')?", Pattern.CASE_INSENSITIVE);

    private static final Pattern TABLE_COLUMN_PATTERN =
            Pattern.compile("`?(\\w+)`?\\s+(varchar|char|tinyint|smallint|mediumint|int|integer|bigint|datetime|decimal)\\b(?:\\(.+?\\))?(?:.*?\\s+COMMENT\\s+'(.*?)')?"
                    , Pattern.CASE_INSENSITIVE);

    public static TableDescriber getTableDescriber(String ddl) {
        String tableName = "";
        String tableComment = "";
        ImmutableList<String> primaryKeys = ImmutableList.of();

        try {
            CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(ddl);
            tableName = createTable.getTable().getName().replaceAll("`", "");

            final int idx = createTable.getTableOptionsStrings().indexOf("COMMENT");
            if (idx != -1) {
                tableComment = (String) createTable.getTableOptionsStrings().get(idx + 2);
            }

            primaryKeys = ImmutableList.copyOf(createTable.getIndexes().stream()
                    .filter(index -> index.getType().equals("PRIMARY KEY"))
                    .map(Index::getColumnsNames)
                    .flatMap(List::stream)
                    .map(pk->pk.replaceAll("`", ""))
                    .collect(Collectors.toList()));
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

        return new TableDescriber(
                Optional.ofNullable(tableName).orElse("default_table_name"),
                Optional.ofNullable(tableComment).orElse(""), primaryKeys);
    }

    public static Set<Property> getProperties(String ddl) {
        final Matcher matcher = TABLE_COLUMN_PATTERN.matcher(ddl);

        Set<Property> properties = Sets.newLinkedHashSet();
        while (matcher.find()) {
            properties.add(
                    new Property(matcher.group(1), mySqlColumnType2JavaType(matcher.group(2)), matcher.group(3)));
        }
        return properties;
    }

    private static final ImmutableMap<String, String> javaTypeMap =
            ImmutableMap.<String, String>builder()
                    .put("tinyint", "Integer")
                    .put("smallint", "Integer")
                    .put("mediumint", "Integer")
                    .put("int", "Integer")
                    .put("integer", "Integer")
                    .put("bigint", "Long")
                    .put("float", "BigDecimal")
                    .put("float precision", "BigDecimal")
                    .put("double", "BigDecimal")
                    .put("double precision", "BigDecimal")
                    .put("decimal", "BigDecimal")
                    .put("varchar", "String")
                    .put("char", "String")
                    .put("datetime", "Date")
                    .put("date", "Date")
                    .put("timestamp", "Date")
                    .build();

    public static String mySqlColumnType2JavaType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        String javaType = javaTypeMap.get(sqlType);
        if (javaType == null)
            throw new IllegalArgumentException("unknown column type:" + sqlType);
        else return javaType;
    }
}
