
info
====================
serverless info -v --stage dev --region eu-west-1 --aws-profile home-sls

deploy service
====================
serverless deploy -v --stage dev --region eu-west-1 --aws-profile home-sls

deploy function
====================
serverless deploy function -f hello -r eu-west-1 -s dev --aws-profile home-sls


invoke
====================
serverless invoke -v --stage dev --region eu-west-1 --aws-profile home-sls --function hello


curl https://5gvwqpvweb.execute-api.eu-west-1.amazonaws.com/dev/sortedTweets


set concurrency for lambda
==========================
aws lambda put-function-concurrency --function-name myTwitterService-dev-hello --reserved-concurrent-executions 10 --profile home-sls


info about lambda
=================
aws lambda get-function --function-name myTwitterService-dev-hello --profile home-sls
{
    "Concurrency": {
        "ReservedConcurrentExecutions": 10
    },
    "Code": {
        "RepositoryType": "S3",
        "Location": "https://awslambda-eu-west-1-tasks.s3.eu-west-1.amazonaws.com/snapshots/487526570401/myTwitterService-dev-hello-dad20e0d-88cf-47f9-b220-4aeba0161839?versionId=3yd2rlJuRntMXhFEzQjn3XiMrKSo.w.b&X-Amz-Security-Token=FQoDYXdzEE0aDFlhDMrwBBwkkpup8iK3A%2BOQKPTYZkNaLnJIgIrPM%2Fu62GPrhpYBxVPosG1ygaD%2BZxDnPtZRa1TW0%2Fnj4giUMrEkxVnoLr4W0X7N9OdXvEQjFmzMa2UF%2BAljFZQGZCMwhQjy2eMVlHTskDtR6dvpYz59vOFrrqw%2B5KosOuTOnV7hMimvi5tzBdu1pUO89gbK%2FZ4KOpp8KLYoOUEppXGyRgGxVQqHcN4dQ25KM%2Fh1hp37XZOCf50KGGywrZeGQM%2BDUw5n%2Bm1%2Btx9gEtXDW8O9KeB%2FsM5v1Wclc1a4aWC6BNnL0mymM6Kby5YLPo%2FexiTLpC0anExDJexgt5ywVGycs7Ms4b2BR5PWKLEL5rpiwRVkGyOrOwb5SuX21MSZ71DdQCRGl8Q7K3%2Bos05SyHNeT19WJrYDDTMK28Z5PTtR9KqS0W0LIFpNvdx5I6WA7KVBPJY6e5XC6IsPFrUUErnfyA6Yp0e93Zlytgw%2BUEqiMoUMHKPGUXBrJXxGMyMMXNA%2FEjwxlvQLOrxDOzqGzEHOEnIgG8xRpC6eDmy2Xm6huKWNRwJpJFlbF9ZSGWf2%2BVr35yWZUeS0KNDO8KqoCjCrymZV94yUtHAoqs%2Fa1QU%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20180324T202813Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=ASIAJ2RHLEYNUL7PUHGA%2F20180324%2Feu-west-1%2Fs3%2Faws4_request&X-Amz-Signature=7022f23a690923515a5679634516f0ab6b8cdba56cb69360e4e798f2655d8e65"
    },
    "Configuration": {
        "TracingConfig": {
            "Mode": "PassThrough"
        },
        "Version": "$LATEST",
        "CodeSha256": "R7yu5JFUON6uQK2WEfDK49+idzp4Ug+S1RxwHTQyNAQ=",
        "FunctionName": "myTwitterService-dev-hello",
        "VpcConfig": {
            "SubnetIds": [],
            "VpcId": "",
            "SecurityGroupIds": []
        },
        "MemorySize": 1024,
        "CodeSize": 3802404,
        "FunctionArn": "arn:aws:lambda:eu-west-1:487526570401:function:myTwitterService-dev-hello",
        "Handler": "com.serverless.Handler",
        "Role": "arn:aws:iam::487526570401:role/myTwitterService-dev-eu-west-1-lambdaRole",
        "Timeout": 60,
        "LastModified": "2018-03-24T12:26:05.270+0000",
        "Runtime": "java8",
        "Description": ""
    },
    "Tags": {
        "STAGE": "dev"
    }