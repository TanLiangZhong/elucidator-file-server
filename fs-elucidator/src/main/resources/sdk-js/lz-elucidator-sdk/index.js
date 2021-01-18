import axios from "axios";

class LzElucidator {

    /**
     * 文件上传
     * @param url 上传地址
     * @param chunkSize 块大小
     */
    constructor(url, chunkSize = 255 * 1024) {
        // TODO 若是文件 2G 文件, chunkSize = 255KB 网络请求过多, 处理方案
        this.url = url
        this.chunkSize = chunkSize
    }

    /**
     * 分片上传
     * @param myFile 文件对象
     * @param progressCallback 上传进度回调函数
     * @return 预览地址
     */
    async uploadPart(myFile, progressCallback) {
        let offset = 0
        let fileId = ''
        let result
        let chunkNum = myFile.size % this.chunkSize === 0 ? myFile.size / this.chunkSize : parseInt((myFile.size / this.chunkSize).toString()) + 1
        for (let chunkIndex = 0; chunkIndex < chunkNum; chunkIndex++) {
            let end = this.chunkSize * (chunkIndex + 1)
            let chunk = myFile.slice(offset, end, myFile.type)
            const formData = new FormData()
            formData.append('size', myFile.size)
            formData.append('name', myFile.name)
            formData.append('chunkSize', this.chunkSize.toString())
            formData.append('fileId', fileId)
            formData.append('file', chunk)
            formData.append('chunkIndex', chunkIndex.toString())

            let {data} = await axios.post(this.url, formData, {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
            if (!data.success) {
                throw new Error(`Fragment upload failed! Error: ${data.msg}`)
            } else {
                progressCallback({
                    progress: ((chunkIndex + 1) / chunkNum).toFixed(2)
                })
            }
            fileId = data.result.fileId
            offset = end
            if (chunkIndex === 0) result = data.result
        }
        return result
    }
}

export default LzElucidator;