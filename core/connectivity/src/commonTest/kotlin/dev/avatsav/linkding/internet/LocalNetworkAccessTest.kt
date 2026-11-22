package dev.avatsav.linkding.internet

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

class LocalNetworkAccessTest {
  @Test
  fun `platforms without this permission never resolve or request local access`() = runTest {
    val access = FakeAccess(required = false)
    access.local = true
    assertEquals(LocalNetworkAccess.State.NotRequired, access.check("https://server.local"))
    assertEquals(0, access.lookups)
  }

  @Test
  fun `public server does not need permission even when local access is denied`() = runTest {
    val access = FakeAccess()
    assertEquals(LocalNetworkAccess.State.NotRequired, access.check("https://public.example"))
  }

  @Test
  fun `private address behind a regular hostname needs permission`() = runTest {
    val access = FakeAccess()
    access.local = true
    assertEquals(
      LocalNetworkAccess.State.Required,
      access.check("https://linkding.internal.example"),
    )
    assertEquals("https://linkding.internal.example", access.lastHost)
  }

  @Test
  fun `check refreshes permission before making an access decision`() = runTest {
    val access = FakeAccess()
    access.local = true
    access.systemGranted = true
    assertEquals(LocalNetworkAccess.State.Granted, access.check("https://server.local"))
    access.systemGranted = false
    assertEquals(LocalNetworkAccess.State.Required, access.check("https://server.local"))
  }

  @Test
  fun `observers react to grant and revocation without opening a dialog`() = runTest {
    val access = FakeAccess()
    access.local = true
    access.observe("https://server.local").test {
      assertEquals(LocalNetworkAccess.State.Required, awaitItem())
      access.systemGranted = true
      access.refresh()
      assertEquals(LocalNetworkAccess.State.Granted, awaitItem())
      access.systemGranted = false
      access.refresh()
      assertEquals(LocalNetworkAccess.State.Required, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `private and link local IPv4 addresses are classified as local`() {
    for (address in
      listOf(
        ip(192, 168, 178, 68),
        ip(10, 0, 0, 1),
        ip(172, 16, 0, 1),
        ip(172, 31, 255, 254),
        ip(169, 254, 1, 1),
      )) assertTrue(isLocalNetworkAddress(address))
  }

  @Test
  fun `public and loopback IPv4 addresses do not require LAN permission`() {
    for (address in
      listOf(
        ip(1, 1, 1, 1),
        ip(172, 15, 0, 1),
        ip(172, 32, 0, 1),
        ip(127, 0, 0, 1),
        ip(0, 0, 0, 0),
      )) assertFalse(isLocalNetworkAddress(address))
  }

  @Test
  fun `IPv6 unique local and link local ranges require LAN permission`() {
    assertTrue(isLocalNetworkAddress(ip(0xfc, 0) + ByteArray(14)))
    assertTrue(isLocalNetworkAddress(ip(0xfd, 0) + ByteArray(14)))
    assertTrue(isLocalNetworkAddress(ip(0xfe, 0x80) + ByteArray(14)))
    assertTrue(isLocalNetworkAddress(ip(0xfe, 0xbf) + ByteArray(14)))
    assertFalse(isLocalNetworkAddress(ip(0x20, 0x01) + ByteArray(14)))
    assertFalse(isLocalNetworkAddress(ByteArray(15) + ip(1)))
  }

  private fun ip(vararg bytes: Int) = bytes.map(Int::toByte).toByteArray()

  private class FakeAccess(private val required: Boolean = true) : LocalNetworkAccess() {
    override val permissionRequired
      get() = required

    override val permissionGranted = MutableStateFlow(false)
    var systemGranted = false
    var local = false
    var lookups = 0
    var lastHost: String? = null

    override suspend fun isLocalServer(hostUrl: String): Boolean {
      lookups++
      lastHost = hostUrl
      return local
    }

    override fun refresh() {
      permissionGranted.value = systemGranted
    }
  }
}
