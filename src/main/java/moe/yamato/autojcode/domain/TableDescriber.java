package moe.yamato.autojcode.domain;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Package: moe.yamato.autojcode
 *
 * @author: 175405@lianjia.com
 * Date: 2017/12/12
 */
public class TableDescriber {

    private final String tableName;

    private final String comment;

    private final ImmutableList<String> primaryKeys;

    public TableDescriber(String tableName, String comment, List<String> primaryKeys) {
        this.tableName = tableName;
        this.comment = comment;
        this.primaryKeys = ImmutableList.copyOf(primaryKeys);
    }

    public String getTableName() {
        return tableName;
    }

    public String getComment() {
        return comment;
    }

    public ImmutableList<String> getPrimaryKeys() {
        return primaryKeys;
    }
}
