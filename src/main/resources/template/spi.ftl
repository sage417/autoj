@PathVariable("id") Integer id,
<#list attrs as param>
@RequestParam("${param.name}") ${param.type} ${param.name},
</#list>