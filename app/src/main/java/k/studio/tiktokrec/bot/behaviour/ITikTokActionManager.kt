package k.studio.tiktokrec.bot.behaviour

interface ITikTokActionManager : TikTokApi {
    fun startActions(onResult:()->Unit)
    fun stopActions()
}
