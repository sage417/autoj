package moe.yamato.autojcode.domain;

/**
 * Package: moe.yamato.autojcode
 *
 * @author: 175405@lianjia.com
 * Date: 2017/12/12
 */
public class TableDescriber {

    private final String tableName;

    private final String comment;

    public TableDescriber(String tableName, String comment) {
        this.tableName = tableName;
        this.comment = comment;
    }

    public String getTableName() {
        return tableName;
    }

    public String getComment() {
        return comment;
    }

}
