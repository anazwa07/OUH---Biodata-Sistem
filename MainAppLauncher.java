2026-05-26T15:23:06.704Z [INFO] ----------------------invoke appmod-report-event----------------------
2026-05-26T15:23:06.705Z [INFO] [Tool.invoke]({name: appmod-report-event, modelId: auto, input: {
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "event": "precheckCompleted",
  "phase": "precheck",
  "status": "succeeded",
  "details": {
    "baseJdkVersion": "17",
    "targetVersion": "Java 25"
  }
}}).
2026-05-26T15:23:06.706Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:23:06.708Z [INFO] Project d:\BiodataOUH\biodataouh is a maven project, try to get java version from pom.xml
2026-05-26T15:23:06.710Z [INFO] [Tool.invoke]({result: {
  "message": "Event 'precheckCompleted' reported!\nSession ID: 20260526152306,\nSession Dir: d:\\BiodataOUH\\biodataouh\\.github\\modernize\\java-upgrade\\20260526152306"
}}).
2026-05-26T15:23:06.711Z [INFO] ----------------------invoked appmod-report-event----------------------
2026-05-26T15:23:06.715Z [INFO] Project java version: 17
2026-05-26T15:23:06.717Z [INFO] Maven project detected.
2026-05-26T15:23:06.718Z [INFO] Build tool type: mvn
2026-05-26T15:23:06.840Z [INFO] JDK: undefined
2026-05-26T15:23:06.841Z [ERROR] Failed to detect or resolve JDK for the project at d:\BiodataOUH\biodataouh.
2026-05-26T15:23:06.842Z [WARN] [ReportEventTool] Full project info unavailable, attempting lightweight fallback: UserError: Failed to detect or resolve JDK for the project at d:\BiodataOUH\biodataouh. Please ensure JDK 17 is installed and properly configured on the machine.
2026-05-26T15:23:06.844Z [INFO] Maven project detected.
2026-05-26T15:23:06.844Z [INFO] Project d:\BiodataOUH\biodataouh is a maven project, try to get java version from pom.xml
2026-05-26T15:23:11.482Z [INFO] ---------------------prepare appmod-report-event----------------------
2026-05-26T15:23:11.482Z [INFO] [Tool.prepareInvocation]({name: appmod-report-event, input: {
  "event": "planGenerationStarted",
  "phase": "plan",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "status": "succeeded"
}}).
2026-05-26T15:23:11.483Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:23:11.485Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "Report upgrade event"
}}).
2026-05-26T15:23:11.487Z [INFO] ----------------------prepared appmod-report-event----------------------
2026-05-26T15:23:11.547Z [INFO] ----------------------invoke appmod-report-event----------------------
2026-05-26T15:23:11.547Z [INFO] [Tool.invoke]({name: appmod-report-event, modelId: auto, input: {
  "event": "planGenerationStarted",
  "phase": "plan",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "status": "succeeded"
}}).
2026-05-26T15:23:11.548Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:23:11.549Z [INFO] [Tool.invoke]({result: {
  "message": "Event 'planGenerationStarted' reported!\nSession ID: 20260526152306,\nSession Dir: d:\\BiodataOUH\\biodataouh\\.github\\modernize\\java-upgrade\\20260526152306"
}}).
2026-05-26T15:23:11.550Z [INFO] ----------------------invoked appmod-report-event----------------------
2026-05-26T15:23:11.572Z [INFO] ---------------------prepare appmod-list-jdks----------------------
2026-05-26T15:23:11.573Z [INFO] [Tool.prepareInvocation]({name: appmod-list-jdks, input: {
  "sessionId": "20260526152306"
}}).
2026-05-26T15:23:11.573Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:23:11.574Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "List jdks"
}}).
2026-05-26T15:23:11.574Z [INFO] ----------------------prepared appmod-list-jdks----------------------
2026-05-26T15:23:11.633Z [INFO] ----------------------invoke appmod-list-jdks----------------------
2026-05-26T15:23:11.633Z [INFO] [Tool.invoke]({name: appmod-list-jdks, modelId: auto, input: {
  "sessionId": "20260526152306"
}}).
2026-05-26T15:23:11.633Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:23:11.657Z [INFO] [Tool.invoke]({result: {
  "message": "We found following jdks in your system:\n\n1. Java 21.0.11\n   📁 Path: C:\\Program Files\\Java\\jdk-21.0.11\\bin\n   🏷️ Source: others\n\n2. Java 25.0.2\n   📁 Path: C:\\Program Files\\Java\\jdk-25.0.2\\bin\n   🏷️ Source: others\n\nTotal: 2 JDK installation(s) found"
}}).
2026-05-26T15:23:11.657Z [INFO] ----------------------invoked appmod-list-jdks----------------------
2026-05-26T15:23:11.679Z [INFO] ---------------------prepare appmod-list-mavens----------------------
2026-05-26T15:23:11.679Z [INFO] [Tool.prepareInvocation]({name: appmod-list-mavens, input: {
  "sessionId": "20260526152306",
  "includeVersion": true
}}).
2026-05-26T15:23:11.680Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:23:11.680Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "List mavens"
}}).
2026-05-26T15:23:11.681Z [INFO] ----------------------prepared appmod-list-mavens----------------------
2026-05-26T15:23:11.759Z [INFO] ----------------------invoke appmod-list-mavens----------------------
2026-05-26T15:23:11.760Z [INFO] [Tool.invoke]({name: appmod-list-mavens, modelId: auto, input: {
  "sessionId": "20260526152306",
  "includeVersion": true
}}).
2026-05-26T15:23:11.760Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:23:11.767Z [INFO] [Tool.invoke]({result: {
  "message": "No Maven installations found."
}}).
2026-05-26T15:23:11.768Z [INFO] ----------------------invoked appmod-list-mavens----------------------
2026-05-26T15:23:56.781Z [INFO] ---------------------prepare appmod-report-event----------------------
2026-05-26T15:23:56.781Z [INFO] [Tool.prepareInvocation]({name: appmod-report-event, input: {
  "event": "planReviewed",
  "phase": "plan",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "status": "succeeded"
}}).
2026-05-26T15:23:56.782Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:23:56.783Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "Report upgrade event"
}}).
2026-05-26T15:23:56.786Z [INFO] ----------------------prepared appmod-report-event----------------------
2026-05-26T15:23:56.865Z [INFO] ----------------------invoke appmod-report-event----------------------
2026-05-26T15:23:56.865Z [INFO] [Tool.invoke]({name: appmod-report-event, modelId: auto, input: {
  "event": "planReviewed",
  "phase": "plan",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "status": "succeeded"
}}).
2026-05-26T15:23:56.866Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:23:56.868Z [INFO] [Tool.invoke]({result: {
  "message": "Event 'planReviewed' reported!\nSession ID: 20260526152306,\nSession Dir: d:\\BiodataOUH\\biodataouh\\.github\\modernize\\java-upgrade\\20260526152306"
}}).
2026-05-26T15:23:56.869Z [INFO] ----------------------invoked appmod-report-event----------------------
2026-05-26T15:23:57.665Z [INFO] ---------------------prepare appmod-confirm-upgrade-plan----------------------
2026-05-26T15:23:57.666Z [INFO] [Tool.prepareInvocation]({name: appmod-confirm-upgrade-plan, input: {
  "sessionId": "20260526152306"
}}).
2026-05-26T15:23:57.669Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:23:57.672Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "Review [upgrade plan](file:///d:/BiodataOUH/biodataouh/.github/modernize/java-upgrade/20260526152306/plan.md)",
  "confirmationMessages": {
    "title": "Plan generated",
    "message": "Please review the generated plan and make any necessary adjustments before proceeding. You can modify the generated [`plan.md`](file:///d:/BiodataOUH/biodataouh/.github/modernize/java-upgrade/20260526152306/plan.md) directly."
  }
}}).
2026-05-26T15:23:57.673Z [INFO] ----------------------prepared appmod-confirm-upgrade-plan----------------------
2026-05-26T15:24:12.578Z [INFO] ----------------------invoke appmod-confirm-upgrade-plan----------------------
2026-05-26T15:24:12.578Z [INFO] [Tool.invoke]({name: appmod-confirm-upgrade-plan, modelId: auto, input: {
  "sessionId": "20260526152306"
}}).
2026-05-26T15:24:12.579Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:24:12.579Z [INFO] Upgrade plan confirmed successfully.
2026-05-26T15:24:12.579Z [INFO] [Tool.invoke]({result: {
  "message": "Generated plan is reviewed and confirmed by the user! the upgrade process will start now."
}}).
2026-05-26T15:24:12.579Z [INFO] ----------------------invoked appmod-confirm-upgrade-plan----------------------
2026-05-26T15:24:24.221Z [INFO] ---------------------prepare appmod-report-event----------------------
2026-05-26T15:24:24.223Z [INFO] [Tool.prepareInvocation]({name: appmod-report-event, input: {
  "event": "planExecutionStarted",
  "phase": "execute",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "status": "succeeded"
}}).
2026-05-26T15:24:24.224Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:24:24.225Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "Report upgrade event"
}}).
2026-05-26T15:24:24.226Z [INFO] ----------------------prepared appmod-report-event----------------------
2026-05-26T15:24:24.645Z [INFO] ----------------------invoke appmod-report-event----------------------
2026-05-26T15:24:24.645Z [INFO] [Tool.invoke]({name: appmod-report-event, modelId: auto, input: {
  "event": "planExecutionStarted",
  "phase": "execute",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "status": "succeeded"
}}).
2026-05-26T15:24:24.646Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:24:24.775Z [INFO] [Tool.invoke]({result: {
  "message": "Event 'planExecutionStarted' reported!\nSession ID: 20260526152306,\nSession Dir: d:\\BiodataOUH\\biodataouh\\.github\\modernize\\java-upgrade\\20260526152306"
}}).
2026-05-26T15:24:24.775Z [INFO] ----------------------invoked appmod-report-event----------------------
2026-05-26T15:24:29.735Z [INFO] ---------------------prepare appmod-report-event----------------------
2026-05-26T15:24:29.736Z [INFO] [Tool.prepareInvocation]({name: appmod-report-event, input: {
  "event": "upgradeStepStarted",
  "phase": "execute",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "details": {
    "stepNumber": 1,
    "stepTitle": "Setup Environment"
  },
  "status": "succeeded"
}}).
2026-05-26T15:24:29.738Z [INFO] [Tool.prepareInvocation] do prepare invocation.
2026-05-26T15:24:29.739Z [INFO] [Tool.prepareInvocation]({result: {
  "invocationMessage": "Report upgrade event"
}}).
2026-05-26T15:24:29.740Z [INFO] ----------------------prepared appmod-report-event----------------------
2026-05-26T15:24:29.892Z [INFO] ----------------------invoke appmod-report-event----------------------
2026-05-26T15:24:29.893Z [INFO] [Tool.invoke]({name: appmod-report-event, modelId: auto, input: {
  "event": "upgradeStepStarted",
  "phase": "execute",
  "projectPath": "d:\\BiodataOUH\\biodataouh",
  "sessionId": "20260526152306",
  "details": {
    "stepNumber": 1,
    "stepTitle": "Setup Environment"
  },
  "status": "succeeded"
}}).
2026-05-26T15:24:29.893Z [INFO] [Tool.invoke] do invoke.
2026-05-26T15:24:29.897Z [INFO] [Tool.invoke]({result: {
  "message": "Event 'upgradeStepStarted' reported!\nSession ID: 20260526152306,\nSession Dir: d:\\BiodataOUH\\biodataouh\\.github\\modernize\\java-upgrade\\20260526152306"
}}).
2026-05-26T15:24:29.897Z [INFO] ----------------------invoked appmod-report-event----------------------
