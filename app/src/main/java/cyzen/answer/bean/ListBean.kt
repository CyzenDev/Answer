package cyzen.answer.bean

class ListBean {
    var category: Any? = null
    var alert: Any? = null
    var title: Any? = null
    var content: Any? = null
    var contentEnd: Any? = null
    var imageStart: Int = 0
    var imageEnd: Int = 0
    var checked: Boolean? = null

    constructor(title: Any, content: Any?) {
        this.title = title
        this.content = content
    }

    constructor(imageStart: Int, title: Any, content: Any?, contentEnd: Any?, imageEnd: Int, checked: Boolean?) {
        this.imageStart = imageStart
        this.title = title
        this.content = content
        this.contentEnd = contentEnd
        this.imageEnd = imageEnd
        this.checked = checked
    }
}