const base = {
    get() {
        return {
            url : "http://localhost:8080/springbootpr919/",
            name: "springbootpr919",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/springbootpr919/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "图书电子售卖网站"
        } 
    }
}
export default base
