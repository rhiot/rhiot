package io.rhiot.utils

import com.github.camellabs.iot.utils.Mavens
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class MavensTest {

    @Test
    void shouldParseCoordinates() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}:${artifact}:${version}"

        // When
        def coordinates = Mavens.MavenCoordinates.parseMavenCoordinates(coordinatesString)

        // Then
        assertThat(coordinates.artifactId()).isEqualTo(artifact)
        assertThat(coordinates.groupId()).isEqualTo(group)
        assertThat(coordinates.version()).isEqualTo(version)
    }

}
