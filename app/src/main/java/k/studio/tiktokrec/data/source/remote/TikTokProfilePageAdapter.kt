package k.studio.tiktokrec.data.source.remote

import k.studio.tiktokrec.data.vo.retrofit.TikTokProfilePage
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * Adapter parse web page and fill data TikTokProfilePage
 */
class TikTokProfilePageAdapter : Converter<ResponseBody, TikTokProfilePage> {
    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): TikTokProfilePage {
        val document: Document = Jsoup.parse(responseBody.string())
        val content: String = document.html()
        return TikTokProfilePage(content, true)
    }

    companion object {
        val FACTORY: Converter.Factory = object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<out Annotation>,
                retrofit: Retrofit
            ): Converter<ResponseBody, *>? {
                return if (type === TikTokProfilePage::class.java) TikTokProfilePageAdapter() else null
            }
        }
    }
}