async function handleUpload() {

    // file 对象
    const myFile = new Blob();

    console.log(' ----myFile----- ', myFile)

    let chunkSize = 255 * 1024
    let chunkNum = myFile.size % chunkSize === 0 ? myFile.size / chunkSize : parseInt((myFile.size / chunkSize).toString()) + 1
    console.log('--chunkNum---', chunkNum)

    let offset = 0
    let fileId = ''
    for (let chunkIndex = 0; chunkIndex < chunkNum; chunkIndex++) {
        let end = chunkSize * (chunkIndex + 1)
        let chunk = myFile.slice(offset, end, myFile.type)
        offset = end
        console.log(' ----chunk----- ', chunk)
        console.log(' ----fileId----- ', fileId)

        const formData = new FormData()
        formData.append('size', myFile.size)
        formData.append('name', myFile.name)
        formData.append('chunkSize', chunkSize.toString())
        formData.append('fileId', fileId)
        formData.append('file', chunk)
        formData.append('chunkIndex', chunkIndex.toString())

        // let res = this.uploadPart(formData)
        let { data } = await axios.post('/v2/uploadPart', formData, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } })
        console.log(data, '==================')
        console.log(data.result.fileId, '==================')
        fileId = data.result.fileId

    }
}