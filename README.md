### WanderWall

This application automatically sets the lockscreen of my BOOX Note Air 3C using a predefined image.

It fetches an image from a specified URL at set intervals. I use this to keep my weekly task list and important reminders accessible at all times.

Since BOOX only pulls lockscreen/power-off images from specific directories, this application requires extensive filesystem access. As such, it is unlikely to be released publicly. If you want to use it, you'll need to build an APK for your device.


I am aware that the wallpaper app offers a feature to pull images from the cloud, but I prefer not to create an account. Additionally, I want more control over how the image is updated, which is why I developed this solution.

I have no experience with Android development, and my solutions might not be ideal. Feel free to suggest improvements.

#### Open Issues

- To save battery, the device disables networking when locked. This causes unnecessary errors in the background tasks that need to be addressed.
- The BOOX wallpaper app doesn't handle updating the same image, so I need to append a timestamp to force updates. Additionally, the code needs to handle cleaning up old downloads.
- Use shared preferences to load defaults instead of hardcoding values.

#### Disclaimer

The code is provided “as is” without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, and noninfringement. In no event shall the authors or copyright holders be liable for any claim, damages, or other liability, whether in an action of contract, tort, or otherwise, arising from, out of, or in connection with the software or the use or other dealings in the software.

This code is licensed under the BSD License.
