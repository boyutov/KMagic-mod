package mc.kmagic.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mc.kmagic.network.ModMessages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class RadialMenuScreen extends Screen {

    private final int SLICES = 10;
    private final float RADIUS = 80.0f;
    private final float INNER_RADIUS = 20.0f;
    private int hoveredSlice = -1;

    public RadialMenuScreen() {
        super(Text.literal("Radial Menu"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double deltaX = mouseX - centerX;
        double deltaY = mouseY - centerY;
        double distanceSq = deltaX * deltaX + deltaY * deltaY;

        hoveredSlice = -1;
        if (distanceSq >= INNER_RADIUS * INNER_RADIUS && distanceSq <= RADIUS * RADIUS) {
            double angle = Math.atan2(deltaY, deltaX);
            double shiftedAngle = angle + Math.PI / 2;
            if (shiftedAngle < 0) {
                shiftedAngle += 2 * Math.PI;
            }
            hoveredSlice = (int) (shiftedAngle / (2 * Math.PI / SLICES)) % SLICES;
        }

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (int i = 0; i < SLICES; i++) {
            float startAngle = (float) (i * 2 * Math.PI / SLICES - Math.PI / 2);
            float endAngle = (float) ((i + 1) * 2 * Math.PI / SLICES - Math.PI / 2);

            boolean isHovered = (i == hoveredSlice);
            
            int r, g, b, a;
            if (isHovered) {
                // Если это кусок 1 (Гроза), подсветим его синеватым электрическим светом
                if (i == 1) {
                    r = 100; g = 200; b = 255; a = 200;
                } else {
                    r = 255; g = 255; b = 255; a = 200;
                }
            } else {
                r = 0; g = 0; b = 0; a = 150;
            }

            drawSlice(matrix, buffer, tessellator, centerX, centerY, startAngle, endAngle, RADIUS, INNER_RADIUS, r, g, b, a);
            drawDivider(matrix, buffer, tessellator, centerX, centerY, startAngle, RADIUS, INNER_RADIUS);
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        if (hoveredSlice != -1) {
            String spellName = hoveredSlice == 1 ? "Заклинание: Гнев Небес" : "Пустой слот (" + hoveredSlice + ")";
            context.drawCenteredTextWithShadow(this.textRenderer, spellName, centerX, centerY - (int)RADIUS - 20, 0xFFFFFF);
        }
    }

    private void drawSlice(Matrix4f matrix, BufferBuilder buffer, Tessellator tessellator, int x, int y, float startAngle, float endAngle, float radius, float innerRadius, int r, int g, int b, int a) {
        int segments = 10;
        float angleStep = (endAngle - startAngle) / segments;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (int j = 0; j < segments; j++) {
            float a1 = startAngle + j * angleStep;
            float a2 = startAngle + (j + 1) * angleStep;

            float x1_out = x + MathHelper.cos(a1) * radius;
            float y1_out = y + MathHelper.sin(a1) * radius;
            float x2_out = x + MathHelper.cos(a2) * radius;
            float y2_out = y + MathHelper.sin(a2) * radius;

            float x1_in = x + MathHelper.cos(a1) * innerRadius;
            float y1_in = y + MathHelper.sin(a1) * innerRadius;
            float x2_in = x + MathHelper.cos(a2) * innerRadius;
            float y2_in = y + MathHelper.sin(a2) * innerRadius;

            buffer.vertex(matrix, x1_in, y1_in, 0).color(r, g, b, a).next();
            buffer.vertex(matrix, x1_out, y1_out, 0).color(r, g, b, a).next();
            buffer.vertex(matrix, x2_out, y2_out, 0).color(r, g, b, a).next();
            buffer.vertex(matrix, x2_in, y2_in, 0).color(r, g, b, a).next();
        }
        tessellator.draw();
    }
    
    private void drawDivider(Matrix4f matrix, BufferBuilder buffer, Tessellator tessellator, int x, int y, float angle, float radius, float innerRadius) {
        float lineThickness = 1.0f;
        
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        
        float x_out = x + cos * radius;
        float y_out = y + sin * radius;
        float x_in = x + cos * innerRadius;
        float y_in = y + sin * innerRadius;
        
        float dx = -sin * lineThickness;
        float dy = cos * lineThickness;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x_in - dx, y_in - dy, 0).color(0, 0, 0, 255).next();
        buffer.vertex(matrix, x_out - dx, y_out - dy, 0).color(0, 0, 0, 255).next();
        buffer.vertex(matrix, x_out + dx, y_out + dy, 0).color(0, 0, 0, 255).next();
        buffer.vertex(matrix, x_in + dx, y_in + dy, 0).color(0, 0, 0, 255).next();
        tessellator.draw();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredSlice != -1) {
            // Отправляем пакет на сервер с номером выбранного заклинания
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(hoveredSlice);
            ClientPlayNetworking.send(ModMessages.CAST_SPELL_ID, buf);
            
            this.close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
