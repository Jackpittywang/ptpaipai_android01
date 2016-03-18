@echo off

SET FILE=PutaoWeidu

SET APK_FOLDER=app\build\outputs\apk

CALL gradle assembleRelease
FOR /f %%i IN (channel.txt) DO (
	if "%%i"=="local_release" (
		CALL del /s /q %APK_FOLDER%\%FILE%.apk
		CALL ren %APK_FOLDER%\app-%%i-release.apk %FILE%.apk
	) else (
		if "%%i"=="local_debug" (
			CALL del /s /q %APK_FOLDER%\%FILE%_DEV.apk
			CALL ren %APK_FOLDER%\app-%%i-release.apk %FILE%_DEV.apk
		) else (
			CALL del /s /q %APK_FOLDER%\%FILE%_%%i.apk
			CALL ren %APK_FOLDER%\app-channel_%%i-release.apk %FILE%_%%i.apk
		)
	)
)

echo Done.
explorer %APK_FOLDER%