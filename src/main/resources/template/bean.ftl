package ${packageName};

import java.io.Serializable;
import java.util.Date;

/**
 * ${classComment}
 */
public class ${className} implements Serializable {

<#list attrs as attr>
	/**
	 * ${attr.comment}
	 */
	private ${attr.type} ${attr.name};

</#list>
<#list attrs as attr>
	/**
	 * 获取 ${attr.comment}
	 * @return ${attr.comment}
	 */
	public ${attr.type} get${attr.name?cap_first}() {
		 return ${attr.name};
	}

	/**
	 * 设置 ${attr.comment}
	 * @param ${attr.name} ${attr.comment}
	 */
	public void set${attr.name?cap_first}(${attr.type} ${attr.name}) {
		 this.${attr.name} = ${attr.name};
	}

</#list>
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("${className}{");
<#list attrs as attr>
		sb.append("${attr.name}='").append(${attr.name}).append('\'')<#sep>.append(", ")</#sep>;
</#list>
		sb.append('}');
		return sb.toString();
	}
}