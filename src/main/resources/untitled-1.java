if(messageCount==1) {                              // ← 第72行
    // ... 一些前置逻辑 ...
    
    chatClient.prompt(prompt)                       // ← 第87行，只有 messageCount==1 才执行
            .user(userMessage)
            .advisors(...)
            .stream()
            .content()
            .doOnNext(...)
            .doOnComplete(...)
            .doOnError(...)
            .subscribe();
}                                                   // ← 第114行，大括号结束