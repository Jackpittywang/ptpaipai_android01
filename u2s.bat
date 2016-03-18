@echo off

SET FILE=PutaoWeidu

SET APK_FOLDER=app\build\outputs\apk
SET HTTP_FOLDER=app\build\outputs\apk

CALL gradle assembleRelease
FOR /f %%i IN (channel.txt) DO (
	if "%%i"=="local_release" (
		CALL del /s /q %APK_FOLDER%\%FILE%.apk
		CALL ren %APK_FOLDER%\app-%%i-release.apk %FILE%.apk
		
		CALL del /s /q %HTTP_FOLDER%\%FILE%.apk
		CALL cp %APK_FOLDER%\%FILE%.apk %HTTP_FOLDER%\%FILE%.apk
	) else (
		if "%%i"=="local_debug" (
			CALL del /s /q %APK_FOLDER%\%FILE%_DEV.apk
			CALL ren %APK_FOLDER%\app-%%i-release.apk %FILE%_DEV.apk
			
			CALL del /s /q %HTTP_FOLDER%\%FILE%_DEV.apk
			CALL cp %APK_FOLDER%\%FILE%_DEV.apk %HTTP_FOLDER%\%FILE%_DEV.apk
		) else (
			CALL del /s /q %APK_FOLDER%\%FILE%_%%i.apk
			CALL ren %APK_FOLDER%\app-channel_%%i-release.apk %FILE%_%%i.apk
			
			CALL del /s /q %HTTP_FOLDER%\channel\%FILE%_%%i.apk
			CALL cp %APK_FOLDER%\%FILE%_%%i.apk %HTTP_FOLDER%\channel\%FILE%_%%i.apk
		)
	)
)

echo Done.
explorer %HTTP_FOLDER%