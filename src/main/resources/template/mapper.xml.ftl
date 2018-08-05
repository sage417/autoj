<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${namespace}">

    <select id="selectById" resultType="${resultType}">
        SELECT
    <#list columns?chunk(3) as rows>
        <#list rows as columnName>`${columnName}`<#sep>, </#sep></#list><#sep>,</#sep>
    </#list>
        FROM `${tableName}`
        WHERE `${pk}` = ${r"#{"}${pk}}
    </select>


    <select id="selectWithId" resultType="${resultType}">
        SELECT
    <#list columns?chunk(3) as rows>
        <#list rows as columnName>`${columnName}`<#sep>, </#sep></#list><#sep>,</#sep>
    </#list>
        FROM `${tableName}`
        WHERE `${pk}` = ${r"#{"}${pk}}
    <#list insertColumns as columnName>
        <if test="columns.contains('${columnName}')">AND `${columnName}` = ${"#\{"}${columnName}}</if>
    </#list>
    </select>


    <insert id="insert" keyProperty="${pk}">
        INSERT INTO `${tableName}`
        (<#list insertColumns as columnName>`${columnName}`<#sep>, </#sep></#list>)
        VALUES
        (
        <#list insertColumns?chunk(3) as rows>
            <#list rows as columnName>#${"\{" + columnName}}<#sep>, </#sep></#list><#sep>, </#sep>
        </#list>
        )
    </insert>

    <insert id="batchInsert" keyProperty="${pk}">
        INSERT INTO `${tableName}`
        (<#list insertColumns as columnName>`${columnName}`<#sep>, </#sep></#list>)
        VALUES
        (
        <foreach collection="list" item="item">
        (<#list insertColumns?chunk(3) as rows>
        <#list rows as columnName>#${"\{item." + columnName}}<#sep>, </#sep></#list><#sep>, </#sep> </#list>)
        </foreach>
        )
    </insert>
    
    <insert id="insertDynamic" keyProperty="${pk}">
        INSERT INTO `${tableName}`
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list insertColumns as columnName>
            <if test="${columnName} != null">`${columnName}`<#sep>,</#sep></if>
        </#list>
        </trim>
        VALUES
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list insertColumns as columnName>
            <if test="${columnName} != null">#${"\{" + columnName}}<#sep>,</#sep></if>
        </#list>
        </trim>
    </insert>

    <update id="update">
        UPDATE `${tableName}`
        <set>
        <#list updateColumns as columnName>
            <if test="columns.contains('${columnName}')">`${columnName}` = ${"#\{entity."}${columnName}},</if>
        </#list>
        </set>
        WHERE `${pk}` = ${r"#{entity."}${pk}}
    </update>

</mapper>
