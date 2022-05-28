package net.coderbot.iris.rendertarget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.coderbot.iris.gl.GlResource;
import net.coderbot.iris.gl.IrisRenderSystem;
import net.coderbot.iris.gl.framebuffer.GlFramebuffer;
import net.coderbot.iris.gl.texture.DepthBufferFormat;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;

public class DepthTexture extends GlResource {
	public DepthTexture(int width, int height, GlFramebuffer copyTexture, DepthBufferFormat format) {
		super(GlStateManager._genTexture());
		GlStateManager._bindTexture(getGlId());

		RenderSystem.texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_NEAREST);
		RenderSystem.texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_NEAREST);
		RenderSystem.texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, GL13C.GL_CLAMP_TO_EDGE);
		RenderSystem.texParameter(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, GL13C.GL_CLAMP_TO_EDGE);
		resize(width, height, copyTexture, format);

		GlStateManager._bindTexture(0);
	}

	void resize(int width, int height, GlFramebuffer copyTexture, DepthBufferFormat format) {
		GlStateManager._bindTexture(getGlId());

		if (copyTexture != null) {
			copyTexture.bindAsReadBuffer();
			IrisRenderSystem.copyTexImage2D(GL20C.GL_TEXTURE_2D, 0, format.getGlInternalFormat(), 0, 0, width, height, 0);
		} else {
			// NB: This isn't correct, but it's much better than garbage data being written to shadowtex0.
			Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
			IrisRenderSystem.copyTexImage2D(GL20C.GL_TEXTURE_2D, 0, format.getGlInternalFormat(), 0, 0, width, height, 0);
		}

		GlStateManager._bindTexture(0);
	}

	public int getTextureId() {
		return getGlId();
	}

	@Override
	protected void destroyInternal() {
		GlStateManager._deleteTexture(getGlId());
	}
}
