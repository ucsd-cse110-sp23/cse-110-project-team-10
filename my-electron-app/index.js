const { app, BrowserWindow } = require('electron')
const path = require('path')
const iconPath = "./icon.png"

function createWindow () {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    icon: iconPath
  })

  win.setBackgroundColor("#EEEBEB")
  win.loadFile('index.html')
}

app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})