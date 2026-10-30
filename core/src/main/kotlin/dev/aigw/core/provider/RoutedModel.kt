package dev.aigw.core.provider

/**
 * 一条对外暴露的模型：`routePrefix/modelId` 是客户端看到的完整 id。
 *
 * 网关用前缀路由到对应 provider，客户端只配一次 base_url 就能用全部供应商。
 * 区域型供应商（如 WorkBuddy 的国内/国外）会拆成多个前缀条目（`codebuddy-cn`、
 * `codebuddy-global`），前缀本身携带区域约束，选号只在该区域的账号中进行。
 */
data class RoutedModel(
    val providerId: String,
    val providerName: String,
    val model: ProviderModel,
    /** 客户端调用用的前缀；普通供应商等于 [providerId]，区域型供应商带区域后缀。 */
    val routePrefix: String = providerId,
    /** 区域标识（如 cn/global）；null 表示该供应商无区域概念。 */
    val region: String? = null,
    /**
     * 本次目录是上游拉取失败后回退的旧清单（不是本次真实拉取结果），界面据此标注「缓存」。
     */
    val cached: Boolean = false,
) {
    val fullId: String get() = "$routePrefix/${model.id}"

    /**
     * 界面展示用的 id。
     *
     * 自定义供应商的 [routePrefix] 带内部标识前缀 `custom:`（如 `custom:mysite`），
     * 这层前缀只用于网关内部路由与实例登记，属于实现细节；直接摊在模型列表里
     * 会让人以为模型名本身长这样。展示时剥掉，复制/调用仍用 [fullId]。
     */
    val displayId: String get() = "${routePrefix.removePrefix(CUSTOM_PREFIX)}/${model.id}"

    companion object {
        /** 自定义供应商 id/路由前缀的统一前缀，与 `ProviderUiRegistry.CUSTOM_PREFIX` 保持一致。 */
        const val CUSTOM_PREFIX = "custom:"

        /** 把 provider id 转成可展示的文本：只剥掉内部的 `custom:` 前缀，其余原样。 */
        fun displayProviderId(providerId: String): String =
            providerId.removePrefix(CUSTOM_PREFIX)
    }
}
