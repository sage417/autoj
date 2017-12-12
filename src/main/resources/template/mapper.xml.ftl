<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperNameSpace}">

    <insert id="insert" keyColumn="id">
        INSERT INTO `${tableName}`
        (<#list insertColumns as columnName>`${columnName}`<#sep>, </#sep></#list>)
        VALUES
        (
        <#list insertColumns?chunk(3) as rows>
            <#list rows as columnName>#${"\{" + columnName}}<#sep>, </#sep></#list><#sep>,</#sep>
        </#list>
        )
    </insert>

    <select id="selectById" resultType="${resultType}">
        SELECT
        <#list columns?chunk(3) as rows>
            <#list rows as columnName>`${columnName}`<#sep>, </#sep></#list><#sep>,</#sep>
        </#list>
        FROM `${tableName}`
        WHERE id = ${r"#{id}"}
    </select>

    <update id="update">
        UPDATE `${tableName}`
        <set>
        <#list updateColumns as columnName>
            <if test="columns.contains('${columnName}')">`${columnName}` = ${"#\{entity."}${columnName}},</if>
        </#list>
        </set>
        WHERE `id` = ${r"#{entity.id}"}
    </update>

</mapper>