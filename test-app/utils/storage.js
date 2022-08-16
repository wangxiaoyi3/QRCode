// const namespace = "test-app" // 命名空間
//
// const storage = {
//     setItemToLocal : function (key, val) {
//         let storageContent = JSON.parse(localStorage.getItem(namespace)) || {}
//         storageContent[key] = val;
//         localStorage.setItem(namespace,JSON.stringify(storageContent))
//         return "存储成功"
//     },
//     getItemToLocal : function (key) {
//         let storageContent = JSON.parse(localStorage.getItem(namespace)) || {}
//         let item = storageContent[key]
//         return item
//     },
//     deleteItemToLocal : function (key) {
//         let storageContent = JSON.parse(localStorage.getItem(namespace)) || {}
//         if (Object.keys(storageContent).length > 0) {
//             delete storageContent[key]
//             setItemToLocal(key,storageContent)
//             return "刪除成功"
//         }
//         return ""
//     }
// }
//
// export default storage