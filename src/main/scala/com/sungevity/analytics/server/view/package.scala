package com.sungevity.analytics.server

import spray.can.server.Stats

import spray.util._

package object view {

  def stats(s: Stats) = <html>
    <body>
      <h1>HttpServer Stats</h1>
      <table>
        <tr><td>uptime:</td><td>{s.uptime.formatHMS}</td></tr>
        <tr><td>totalRequests:</td><td>{s.totalRequests}</td></tr>
        <tr><td>openRequests:</td><td>{s.openRequests}</td></tr>
        <tr><td>maxOpenRequests:</td><td>{s.maxOpenRequests}</td></tr>
        <tr><td>totalConnections:</td><td>{s.totalConnections}</td></tr>
        <tr><td>openConnections:</td><td>{s.openConnections}</td></tr>
        <tr><td>maxOpenConnections:</td><td>{s.maxOpenConnections}</td></tr>
        <tr><td>requestTimeouts:</td><td>{s.requestTimeouts}</td></tr>
      </table>
    </body>
  </html>

  def index = <html>
    <body>
      <h1>Say hello to <i>spray-can</i>!</h1>
      <p>Defined resources:</p>
      <ul>
        <li><a href="/ping">/ping</a></li>
        <li><a href="/stream">/stream</a></li>
        <li><a href="/server-stats">/server-stats</a></li>
        <li><a href="/crash">/crash</a></li>
        <li><a href="/timeout">/timeout</a></li>
        <li><a href="/timeout/timeout">/timeout/timeout</a></li>
        <li><a href="/stop">/stop</a></li>
      </ul>
      <p>Test file upload</p>
      <form action ="/file-upload" enctype="multipart/form-data" method="post">
        <input type="file" name="datafile" multiple=""></input>
        <br/>
        <input type="submit">Submit</input>
      </form>
    </body>
  </html>

}
